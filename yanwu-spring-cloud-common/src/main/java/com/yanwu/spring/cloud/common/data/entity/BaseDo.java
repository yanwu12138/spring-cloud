package com.yanwu.spring.cloud.common.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;

@Data
@ToString
@EqualsAndHashCode(callSuper = false, of = {})
@MappedSuperclass
public abstract class BaseDo<PK extends Serializable> extends BaseTimeStamp implements BaseObject, OperationLogAware {

    private static final long serialVersionUID = -4088076756365147614L;

    public static final String[] PROPERTIES = {"id", "createdAt", "updatedAt", "enable"};

    static public class LongIdComparator implements Comparator<BaseDo<Long>> {
        @Override
        public int compare(BaseDo<Long> o1, BaseDo<Long> o2) {
            if (o1.getId() == null) {
                return o2.getId() == null ? 0 : -1;
            }
            return o2.getId() == null ? 1 : o1.getId().compareTo(o2.getId());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    @Column(name = "ID", unique = true, nullable = false)
    private PK id;

    @Getter
    @Setter
    @Column(name = "ENABLE", nullable = false)
    private Boolean enable;

    @JsonIgnore
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String getLogging() {
        return String.valueOf(id);
    }

    @Override
    @PreUpdate
    protected void onCreate() {
        super.onCreate();
        if (enable == null) {
            enable = true;
        }
    }
}