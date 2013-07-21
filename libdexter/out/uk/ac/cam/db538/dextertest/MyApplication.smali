.class public Luk/ac/cam/db538/dextertest/MyApplication;
.super Landroid/app/Application;
.source "MyApplication.java"


# static fields
.field public static Timer_AppInit:J

.field public static Timer_ButtonClick:J


# direct methods
.method static constructor <clinit>()V
    .registers 2

    .prologue
    .line 10
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v0

    sput-wide v0, Luk/ac/cam/db538/dextertest/MyApplication;->Timer_AppInit:J

    .line 5
    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 5
    invoke-direct {p0}, Landroid/app/Application;-><init>()V

    return-void
.end method
