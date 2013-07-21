.class public Luk/ac/cam/db538/dextertest/Sink_ApacheHTTPClient;
.super Ljava/lang/Object;
.source "Sink_ApacheHTTPClient.java"

# interfaces
.implements Luk/ac/cam/db538/dextertest/Sink;


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public sendData(Ljava/lang/String;Landroid/content/Context;)V
    .registers 12
    .parameter "data"
    .parameter "context"

    .prologue
    .line 20
    const/4 v5, 0x1

    new-array v5, v5, [Lorg/apache/http/NameValuePair;

    const/4 v6, 0x0

    new-instance v7, Lorg/apache/http/message/BasicNameValuePair;

    const-string v8, "postedData"

    invoke-direct {v7, v8, p1}, Lorg/apache/http/message/BasicNameValuePair;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    aput-object v7, v5, v6

    invoke-static {v5}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v2

    .line 21
    .local v2, nameValuePairs:Ljava/util/List;,"Ljava/util/List<Lorg/apache/http/NameValuePair;>;"
    const/4 v4, 0x0

    .line 23
    .local v4, postData:Lorg/apache/http/HttpEntity;
    :try_start_12
    new-instance v4, Lorg/apache/http/client/entity/UrlEncodedFormEntity;

    .end local v4           #postData:Lorg/apache/http/HttpEntity;
    invoke-direct {v4, v2}, Lorg/apache/http/client/entity/UrlEncodedFormEntity;-><init>(Ljava/util/List;)V
    :try_end_17
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_12 .. :try_end_17} :catch_2a

    .line 29
    .restart local v4       #postData:Lorg/apache/http/HttpEntity;
    new-instance v3, Lorg/apache/http/client/methods/HttpPost;

    const-string v5, "http://www.google.com/"

    invoke-direct {v3, v5}, Lorg/apache/http/client/methods/HttpPost;-><init>(Ljava/lang/String;)V

    .line 30
    .local v3, post:Lorg/apache/http/client/methods/HttpPost;
    invoke-virtual {v3, v4}, Lorg/apache/http/client/methods/HttpPost;->setEntity(Lorg/apache/http/HttpEntity;)V

    .line 32
    new-instance v0, Lorg/apache/http/impl/client/DefaultHttpClient;

    invoke-direct {v0}, Lorg/apache/http/impl/client/DefaultHttpClient;-><init>()V

    .line 34
    .local v0, client:Lorg/apache/http/impl/client/DefaultHttpClient;
    :try_start_26
    invoke-virtual {v0, v3}, Lorg/apache/http/impl/client/DefaultHttpClient;->execute(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/HttpResponse;
    :try_end_29
    .catch Ljava/lang/Throwable; {:try_start_26 .. :try_end_29} :catch_34

    .line 38
    :goto_29
    return-void

    .line 24
    .end local v0           #client:Lorg/apache/http/impl/client/DefaultHttpClient;
    .end local v3           #post:Lorg/apache/http/client/methods/HttpPost;
    .end local v4           #postData:Lorg/apache/http/HttpEntity;
    :catch_2a
    move-exception v1

    .line 25
    .local v1, e:Ljava/io/UnsupportedEncodingException;
    invoke-virtual {v1}, Ljava/io/UnsupportedEncodingException;->printStackTrace()V

    .line 26
    new-instance v5, Ljava/lang/Error;

    invoke-direct {v5, v1}, Ljava/lang/Error;-><init>(Ljava/lang/Throwable;)V

    throw v5

    .line 35
    .end local v1           #e:Ljava/io/UnsupportedEncodingException;
    .restart local v0       #client:Lorg/apache/http/impl/client/DefaultHttpClient;
    .restart local v3       #post:Lorg/apache/http/client/methods/HttpPost;
    .restart local v4       #postData:Lorg/apache/http/HttpEntity;
    :catch_34
    move-exception v5

    goto :goto_29
.end method
