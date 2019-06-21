package com.yanwu.spring.cloud.common.data.init;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.data.repository.ModelMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * Support 2 ways to import data into RDB
 * 1. object mode, json file format
 * 2. sql mode,sql line
 *
 * @author gongyf
 */
@Slf4j
public class DBInitialProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private static final String DEFAULT_DB_IMPORT_FILE = "db_init.json";

    @Autowired
    ModelMetadata metaData;

    @Autowired
    EntityManagerFactory emf;

    EntityManager em = null;

    private static boolean isInit = false;

    public DBInitialProcessor() {
        super();
    }

    /*	@PostConstruct
        public void dbInit() {
    */
    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {

        if (!isInit) {
            isInit = true;
        } else {
            return;
        }

        log.info("Run once, DBInitialProcessor:onApplicationEvent");
        // avoid execute it twice,
        // if (event.getApplicationContext().getParent() == null) {

        // read general JSON file
        FileReader generalReader = new FileReader();

        List<Object> procList = generalReader.readFromFile(DEFAULT_DB_IMPORT_FILE,
                TableProcessor.class);

        if (procList == null) {
            log.info("no db data to be imported {} ", DEFAULT_DB_IMPORT_FILE);
            return;
        }

        em = emf.createEntityManager();

        for (Object proc : procList) {

            try {
                // read each definition file
                if (proc instanceof TableProcessor) {

                    TableProcessor tableProc = (TableProcessor) proc;
                    if (tableProc.getFileType() == FileType.JSON) {
                        doObjectImport(tableProc);
                    } else if (tableProc.getFileType() == FileType.SQL) {
                        doSQLImport(tableProc);
                    }

                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        em.close();

    }

    private void doSQLImport(TableProcessor tableProc) {
        log.info("doSQLImport {} ", tableProc);

//		EntityManager em= null;
        try {
            Class claz = Class.forName(tableProc.getModelClassName());
            JpaRepository rep = metaData.getRepository(claz);

            long tableCount = rep.count();

            if (tableCount == 0) {

                FileReader sqlReader = new FileReader();

                List<String> sqlList = sqlReader.readSQLFromFile(tableProc.getDataFileName());

                if (sqlList != null) {
//					em=emf.createEntityManager();

                    try {
                        em.getTransaction().begin();

                        for (String sql : sqlList) {
                            Query query = (Query) em.createNativeQuery(sql);
                            query.executeUpdate();
                            //							em.flush();
                        }
                        em.getTransaction().commit();
                        //					em.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (em != null) {
                            em.getTransaction().rollback();
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void doObjectImport(TableProcessor tableProc) {

        log.info("doObjectImport {} ", tableProc);
        try {

            FileReader tableReader = new FileReader();

            Class claz = Class.forName(tableProc.getModelClassName());

            List<Object> objList = tableReader.readFromFile(tableProc.getDataFileName(), claz);

            // call repository to save
            // find repository by class name

            JpaRepository rep = metaData.getRepository(claz);

            long tableCount = rep.count();

            log.info("rep= {},tableCount={},objList={}", rep, tableCount, objList);

            if (tableCount == 0 && objList != null) {
                for (Object obj : objList) {

                    rep.save(obj);
                    log.info("save {}", obj);
                }
            }

            // log and
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
