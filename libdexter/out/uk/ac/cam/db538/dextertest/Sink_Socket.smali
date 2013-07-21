.class public Luk/ac/cam/db538/dextertest/Sink_Socket;
.super Ljava/lang/Object;
.source "Sink_Socket.java"

# interfaces
.implements Luk/ac/cam/db538/dextertest/Sink;


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public sendData(Ljava/lang/String;Landroid/content/Context;)V
    .registers 11
    .parameter "data"
    .parameter "context"

    .prologue
    .line 15
    :try_start_0
    new-instance v2, Ljava/net/Socket;

    const-string v6, "www.google.com"

    const/16 v7, 0x50

    invoke-direct {v2, v6, v7}, Ljava/net/Socket;-><init>(Ljava/lang/String;I)V

    .line 16
    .local v2, s1:Ljava/net/Socket;
    invoke-virtual {v2}, Ljava/net/Socket;->getOutputStream()Ljava/io/OutputStream;

    move-result-object v4

    .line 17
    .local v4, sos1:Ljava/io/OutputStream;
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v6

    invoke-virtual {v4, v6}, Ljava/io/OutputStream;->write([B)V

    .line 18
    invoke-virtual {v4}, Ljava/io/OutputStream;->close()V

    .line 19
    invoke-virtual {v2}, Ljava/net/Socket;->close()V

    .line 21
    new-instance v3, Ljava/net/Socket;

    const-string v6, "www.google.com"

    const/16 v7, 0x50

    invoke-direct {v3, v6, v7}, Ljava/net/Socket;-><init>(Ljava/lang/String;I)V

    .line 22
    .local v3, s2:Ljava/net/Socket;
    invoke-virtual {v3}, Ljava/net/Socket;->getOutputStream()Ljava/io/OutputStream;

    move-result-object v5

    .line 23
    .local v5, sos2:Ljava/io/OutputStream;
    new-instance v1, Ljava/io/PrintWriter;

    invoke-direct {v1, v5}, Ljava/io/PrintWriter;-><init>(Ljava/io/OutputStream;)V

    .line 24
    .local v1, pw:Ljava/io/PrintWriter;
    invoke-virtual {v1, p1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 25
    invoke-virtual {v1}, Ljava/io/PrintWriter;->close()V

    .line 26
    invoke-virtual {v5}, Ljava/io/OutputStream;->close()V

    .line 27
    invoke-virtual {v3}, Ljava/net/Socket;->close()V
    :try_end_38
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_38} :catch_39

    .line 31
    return-void

    .line 28
    .end local v1           #pw:Ljava/io/PrintWriter;
    .end local v2           #s1:Ljava/net/Socket;
    .end local v3           #s2:Ljava/net/Socket;
    .end local v4           #sos1:Ljava/io/OutputStream;
    .end local v5           #sos2:Ljava/io/OutputStream;
    :catch_39
    move-exception v0

    .line 29
    .local v0, e:Ljava/lang/Exception;
    new-instance v6, Ljava/lang/RuntimeException;

    invoke-direct {v6, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v6
.end method
