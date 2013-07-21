.class public Luk/ac/cam/db538/dextertest/Sink_FileSystem;
.super Ljava/lang/Object;
.source "Sink_FileSystem.java"

# interfaces
.implements Luk/ac/cam/db538/dextertest/Sink;


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public sendData(Ljava/lang/String;Landroid/content/Context;)V
    .registers 11
    .parameter "data"
    .parameter "context"

    .prologue
    .line 16
    :try_start_0
    new-instance v1, Ljava/io/File;

    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    move-result-object v6

    const-string v7, "dexter.test1"

    invoke-direct {v1, v6, v7}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    .line 17
    .local v1, file1:Ljava/io/File;
    new-instance v3, Ljava/io/FileOutputStream;

    invoke-direct {v3, v1}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V

    .line 18
    .local v3, fos1:Ljava/io/FileOutputStream;
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v6

    invoke-virtual {v3, v6}, Ljava/io/FileOutputStream;->write([B)V

    .line 19
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V

    .line 21
    new-instance v2, Ljava/io/File;

    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    move-result-object v6

    const-string v7, "dexter.test2"

    invoke-direct {v2, v6, v7}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    .line 22
    .local v2, file2:Ljava/io/File;
    new-instance v4, Ljava/io/FileOutputStream;

    invoke-direct {v4, v2}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V

    .line 23
    .local v4, fos2:Ljava/io/FileOutputStream;
    new-instance v5, Ljava/io/PrintWriter;

    invoke-direct {v5, v4}, Ljava/io/PrintWriter;-><init>(Ljava/io/OutputStream;)V

    .line 24
    .local v5, pw:Ljava/io/PrintWriter;
    invoke-virtual {v5, p1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 25
    invoke-virtual {v5}, Ljava/io/PrintWriter;->close()V

    .line 26
    invoke-virtual {v4}, Ljava/io/FileOutputStream;->close()V
    :try_end_38
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_38} :catch_39

    .line 30
    return-void

    .line 27
    .end local v1           #file1:Ljava/io/File;
    .end local v2           #file2:Ljava/io/File;
    .end local v3           #fos1:Ljava/io/FileOutputStream;
    .end local v4           #fos2:Ljava/io/FileOutputStream;
    .end local v5           #pw:Ljava/io/PrintWriter;
    :catch_39
    move-exception v0

    .line 28
    .local v0, e:Ljava/lang/Exception;
    new-instance v6, Ljava/lang/RuntimeException;

    invoke-direct {v6, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v6
.end method
