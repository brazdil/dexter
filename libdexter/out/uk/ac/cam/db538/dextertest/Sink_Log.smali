.class public Luk/ac/cam/db538/dextertest/Sink_Log;
.super Ljava/lang/Object;
.source "Sink_Log.java"

# interfaces
.implements Luk/ac/cam/db538/dextertest/Sink;


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public sendData(Ljava/lang/String;Landroid/content/Context;)V
    .registers 8
    .parameter "data"
    .parameter "context"

    .prologue
    .line 9
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v0

    .line 10
    .local v0, timer:J
    const-string v2, "Dexter"

    invoke-static {v2, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 11
    const-string v2, "TIMER_LOG"

    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v3

    sub-long/2addr v3, v0

    invoke-static {v3, v4}, Ljava/lang/Long;->toString(J)Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 12
    return-void
.end method
