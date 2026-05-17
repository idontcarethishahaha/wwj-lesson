package org.example.util;

import com.alipay.easysdk.kernel.Config;

/**
 * 类说明：阿里支付工具类 - 沙箱环境
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-17 10:10
 */
public class AlipayUtil {
    private static final String APP_ID = "9021000163680017";//应用id
    // 支付成功后的回调地址
    private static final String NOTIFY_URL = "http://78c477ec.r15.cpolar.top/api/v1/order/prePayNotify";
    // 支付宝的公钥
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqeKaaQ5hG8dcp0wWKfOWAULrn461rybNmfaael6lc1qLtIy/8WAbxpXAys6VnU7mAIslJThNMrUELZWljlhFl9AZAGxjfRHlpkBTr3zPYGZmPu9VImvaZgaDj1CSk+b85ODRQh+P0zRf7Q3fIXxLmGS7bedWYIEcRYrI0VD2XQvjQo8r4yORRU8v6xKasnXoVwkuBSXbnyLbaNRcGVTRTVV4T5lGE5rNzfLDZ+ykle1SsmN8C8cFTwyMphDMveOT6JP8qmmfc6NjAnOzAsC5qR0u5C8MLktoRHCfY68t1I4acr8rZ++zjN3bkAEjc/6N/kIG9ah6/xlUUmUHvMgcqwIDAQAB";
    // 商户私钥           应用私钥-java
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuqxYT/E4gCZ6S87EM/vJNsP8iaUusWfMm0L9zMQJEUcZNj9M6kx0Hl/oQaintn+dGZCDVv1wwGy8Xx+qJHgPN6twBjFCh5vPyIaEg/RXf3rW651LnySKQPG69whkB00nmyrSQPFGmaR3SX6iqV5jw3zfEZmTEI+ulKDi4UYKl/0KyY2MmvbNlBjg1yl5r4ZxKgmROBqm7kY6bNg95ib07n3DYoCnGkhtAv8tBXUH5lCTJq6aMPj8C6fm5Z0rRQmbyZPM6WpuxxAv12xRSdazz2w3W3q9C3il+QbMAZTqlpLnfqQezq55diOszre+70ydgGYwB1TWDOmwDjbH7HL23AgMBAAECggEAIQQt9DEpA11OIDD4MqCuZIkkcPi+WSHXmGq1Ba0p5Iggl+Cr6NKOFjVgC2JF8qjbKq8alczKPD2nNTOQ3cJSrcXLOG+xfikNy6MXtgig7eRHLcE0JqQaaHFD7ZKW74Vk3r9KSklclbwg6vcyBqDwQ/etru479edkT5++uPiPXZkGrSKWd2NMQws++qZrJ8ACvg9XcLACUxgN8zaQrm2X3mk06bxjhOzNjJiS7Q2kEAI4zKjoiYvTk8ypN64HPU+xl9HG3U9FRep3Bc+JOPo4zOXn3WaEdndsDEkGPuvUKZ4vzLJLO2UglAasC6abGI9GIHA27VdQgcqMv+Wuo2XLgQKBgQDT1wbwep7zk0rCEFvP/TYqKy0OjjD2qyfn1yukwKTEAO/rUf+xlSyXCO4KcsBzm2B3P+0tpcNar9g3YCvv33WTZeYT5Vq4zI5ovybkRa4FffMJtHmaqg9L6xnaY1E3qtLm+8s2ym8Zn4M76VUydyKWYJrcCYbYK8/zOZ+ehvNFhwKBgQDTFF7vnUmTIlYpETvZHNpYdG5EeQGHr5Fjvj5poIuR50JCGL5zCnTsQkX74DUganiZo9RaQ+EJh43P0LErQ1NWfkp+JFnZsgxJr7+yj3pUZ6H8oGcw7d5FZxNLQ9gvLF4R0WPPeJABnmDwytRCnf80LRM93bnJBoK4t7HDlM3SUQKBgDk9JJbdyw0tg0mkeepdFMGYIPiJ2xkLbiDqttQxaVO8nRcMdPZaxE2bEBaqou2Z0hrmnwNygrKCSP3NcpHB41cMdXpywmykeOY8oBmV6kt5mK3KogD0amWtBLNJmqxb79iRuHr5f8OAokkR5v9XyXSwbad+u3y1ed7SdxqyhBEJAoGAR5DEj2E4rNYbeFkLfZU2wn7XVAfi6Oox1SuPZai1KYbLBDX3fHGzKtddewSNfqwar5zIRsJ07hIzlp4fM5fkDZNMFnCkthaT6/fWeugChdONLGYGFqTPk6Ba/k3GYlgcnNXtjU5HJQVZwMpEJUIEYOBXgcZHU00NzSsa7kDpd8ECgYEAnfQbTDwH1nR92voFGlA7ShjgIsVa4FlvLRAu7S3uDwI/1XGzi+mKEKaeofHgiTvpD+OBjom9qoxxELh0Oti+zkxiD3q3f3GT/kpaKNGwZ6SXar+PHDnm6wKsZFj7bVZCf2XyvwZ31n7FmYwTy6wfok4qV598uXWIcIvwUNYCw78=";
    private static volatile Config config; // 做成一个单例
    // 单例模式的获取实例
    public static Config getInstance() {// 单例模式 - 懒汉式
        // 双重检查锁
        if(config == null){// 第一重检查
            synchronized (AlipayUtil.class){
                if(config == null){// 第二重检查，避免重复创建实例
                    config = new Config();// 创建Config实例
                    config.protocol = "https";// 设置协议
                    config.gatewayHost = "openapi-sandbox.dl.alipaydev.com";//设置支付宝网关地址
                    config.appId = APP_ID;// 设置应用ID
                    config.signType = "RSA2";// 设置签名算法
                    config.alipayPublicKey = ALIPAY_PUBLIC_KEY;// 支付宝公钥
                    config.merchantPrivateKey = PRIVATE_KEY;// 商户私钥
                    config.notifyUrl = NOTIFY_URL;// 设置回调地址
                    config.ignoreSSL = true;// 忽略证书
                }
            }
        }
        return config;
    }
}
