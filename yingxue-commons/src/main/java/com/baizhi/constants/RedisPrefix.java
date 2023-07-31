package com.baizhi.constants;

public interface RedisPrefix {

    //验证码
    String CODE_KEY = "CODE:";

    //Token
    String TOKEN_KEY = "TOKEN:";

    //点赞
    String LIKE_KEY = "LIKE:";

    //播放
    String PLAYED_KEY = "PLAYED:";

    //是否点赞
    String IS_LIKE_KEY = "IS_LIKE:";

    //点赞数
    String LIKE_COUNT_KEY = "LIKE_COUNT:";

    //不喜欢
    String IS_DISLIKE_KEY = "IS_DISLIKE:";

}
