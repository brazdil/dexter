.class Luk/ac/cam/db538/dextertest/MainActivity$1$1;
.super Landroid/os/Handler;
.source "MainActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Luk/ac/cam/db538/dextertest/MainActivity$1;->onClick(Landroid/view/View;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

.field private final synthetic val$context:Landroid/content/Context;

.field private final synthetic val$dlgProgress:Landroid/app/ProgressDialog;


# direct methods
.method constructor <init>(Luk/ac/cam/db538/dextertest/MainActivity$1;Landroid/app/ProgressDialog;Landroid/content/Context;)V
    .registers 4
    .parameter
    .parameter
    .parameter

    .prologue
    .line 1
    iput-object p1, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1;->this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

    iput-object p2, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1;->val$dlgProgress:Landroid/app/ProgressDialog;

    iput-object p3, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1;->val$context:Landroid/content/Context;

    .line 48
    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .registers 10
    .parameter "msg"

    .prologue
    .line 51
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1;->val$dlgProgress:Landroid/app/ProgressDialog;

    invoke-virtual {v4}, Landroid/app/ProgressDialog;->dismiss()V

    .line 53
    new-instance v0, Landroid/app/AlertDialog$Builder;

    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1;->val$context:Landroid/content/Context;

    invoke-direct {v0, v4}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    .line 54
    .local v0, dlgBuilder:Landroid/app/AlertDialog$Builder;
    const v4, 0x7f040009

    new-instance v5, Luk/ac/cam/db538/dextertest/MainActivity$1$1$1;

    invoke-direct {v5, p0}, Luk/ac/cam/db538/dextertest/MainActivity$1$1$1;-><init>(Luk/ac/cam/db538/dextertest/MainActivity$1$1;)V

    invoke-virtual {v0, v4, v5}, Landroid/app/AlertDialog$Builder;->setPositiveButton(ILandroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    .line 61
    iget v4, p1, Landroid/os/Message;->what:I

    if-nez v4, :cond_40

    .line 62
    const v4, 0x7f040008

    invoke-virtual {v0, v4}, Landroid/app/AlertDialog$Builder;->setMessage(I)Landroid/app/AlertDialog$Builder;

    .line 63
    const v4, 0x7f040007

    invoke-virtual {v0, v4}, Landroid/app/AlertDialog$Builder;->setTitle(I)Landroid/app/AlertDialog$Builder;

    .line 69
    :cond_27
    :goto_27
    invoke-virtual {v0}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;

    move-result-object v1

    .line 70
    .local v1, dlgLeakage:Landroid/app/AlertDialog;
    invoke-virtual {v1}, Landroid/app/AlertDialog;->show()V

    .line 72
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v4

    sget-wide v6, Luk/ac/cam/db538/dextertest/MyApplication;->Timer_ButtonClick:J

    sub-long v2, v4, v6

    .line 73
    .local v2, timeExecution:J
    const-string v4, "TIMER_EXECUTION"

    invoke-static {v2, v3}, Ljava/lang/Long;->toString(J)Ljava/lang/String;

    move-result-object v5

    invoke-static {v4, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 74
    return-void

    .line 64
    .end local v1           #dlgLeakage:Landroid/app/AlertDialog;
    .end local v2           #timeExecution:J
    :cond_40
    iget v4, p1, Landroid/os/Message;->what:I

    if-eqz v4, :cond_27

    .line 65
    const-string v4, "An error occured. Check logcat."

    invoke-virtual {v0, v4}, Landroid/app/AlertDialog$Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    .line 66
    const-string v4, "Error"

    invoke-virtual {v0, v4}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    goto :goto_27
.end method
