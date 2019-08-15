package com.yanwu.spring.cloud.common.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Administrator
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false, of = {})
@MappedSuperclass
public abstract class BaseDo<PK extends Serializable> extends BaseTimeStamp implements BaseObject, OperationLogAware {

    private static final long serialVersionUID = -4088076756365147614L;

    public static final String[] PROPERTIES = {"id", "createdAt", "updatedAt"};

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @Column(name = "ID")
    private PK id;

    @JsonIgnore
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String getLogging() {
        return String.valueOf(id);
    }
}