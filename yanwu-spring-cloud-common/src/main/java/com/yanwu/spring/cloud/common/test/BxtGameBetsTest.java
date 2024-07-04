package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.RestUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XuBaofeng.
 * @date 2024/7/4 15:16.
 * <p>
 * description:
 */
@Slf4j
public class BxtGameBetsTest {

    private static final String APP_ID = "95759364912136190728";
    private static final String SECRET = "7DsfRARziApF6cstMpBN2zslTA63ljBo";
    private static final String GAME_BETS_START_URL = "http://test1bxtapi.boxingtong.net:6241/bxt-api/game/start";
    private static final String GAME_SETTLEMENT_URL = "http://test1bxtapi.boxingtong.net:6241/bxt-api/game/settlement";
    private static final String[] USERS = new String[]{"C10000336", "C10091931"};

    public static void main(String[] args) throws InterruptedException {
        String betsId = RandomStringUtils.randomAlphanumeric(24);
        Thread thread336 = new Thread(() -> gameStart(betsId, "C10000336", RandomUtils.nextLong(1_000L, 3_000L)));
        Thread thread931 = new Thread(() -> gameStart(betsId, "C10091931", RandomUtils.nextLong(2_000L, 5_000L)));

        thread336.start();
        thread931.start();

        thread336.join();
        thread931.join();

        new Thread(() -> settlement(betsId, USERS[RandomUtils.nextInt(0, 2)], RandomUtils.nextLong(5_000L, 10_000L))).start();
    }

    private static void settlement(String betsId, String cid, long sleep) {
        ThreadUtil.sleep(sleep);
        RequestInfo<Object, Object> instance = RequestInfo.getInstance(HttpMethod.POST, GAME_SETTLEMENT_URL, Object.class);
        Result<Object> result = RestUtil.execute(buildHeaders(instance).buildBody(buildBody(betsId, cid)));
        log.info("game settlement, sleep: {}, betsId: {}, cid: {}, result: {}", sleep, betsId, cid, result);
    }

    private static void gameStart(String betsId, String cid, long sleep) {
        ThreadUtil.sleep(sleep);
        RequestInfo<Object, Object> instance = RequestInfo.getInstance(HttpMethod.POST, GAME_BETS_START_URL, Object.class);
        Result<Object> result = RestUtil.execute(buildHeaders(instance).buildBody(buildBody(betsId, cid)));
        log.info("game start, sleep: {}, betsId: {}, cid: {}, result: {}", sleep, betsId, cid, result);
    }

    private static RequestInfo<Object, Object> buildHeaders(RequestInfo<Object, Object> instance) {
        long timestamp = System.currentTimeMillis();
        instance.buildHeaders("Content-Type", "application/json").buildHeaders("AppId", APP_ID)
                .buildHeaders("Timestamp", String.valueOf(timestamp)).buildHeaders("BXT_Token", token(timestamp));
        return instance;
    }

    private static Map<String, String> buildBody(String betsId, String cid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", cid);
        param.put("betsId", betsId);
        return param;
    }

    private static String token(long timestamp) {
        String token = DigestUtils.md5Hex((APP_ID + SECRET).getBytes(StandardCharsets.UTF_8)).toLowerCase();
        return DigestUtils.md5Hex((token + timestamp).getBytes(StandardCharsets.UTF_8)).toLowerCase();
    }

}
