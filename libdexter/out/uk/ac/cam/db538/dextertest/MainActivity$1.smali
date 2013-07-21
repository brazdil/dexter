.class Luk/ac/cam/db538/dextertest/MainActivity$1;
.super Ljava/lang/Object;
.source "MainActivity.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Luk/ac/cam/db538/dextertest/MainActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Luk/ac/cam/db538/dextertest/MainActivity;


# direct methods
.method constructor <init>(Luk/ac/cam/db538/dextertest/MainActivity;)V
    .registers 2
    .parameter

    .prologue
    .line 1
    iput-object p1, p0, Luk/ac/cam/db538/dextertest/MainActivity$1;->this$0:Luk/ac/cam/db538/dextertest/MainActivity;

    .line 42
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method static synthetic access$0(Luk/ac/cam/db538/dextertest/MainActivity$1;)Luk/ac/cam/db538/dextertest/MainActivity;
    .registers 2
    .parameter

    .prologue
    .line 42
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity$1;->this$0:Luk/ac/cam/db538/dextertest/MainActivity;

    return-object v0
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .registers 10
    .parameter "v"

    .prologue
    .line 45
    invoke-virtual {p1}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    .line 46
    .local v0, context:Landroid/content/Context;
    const-string v4, "Working.."

    const-string v5, "Leaking data"

    const/4 v6, 0x1

    const/4 v7, 0x0

    invoke-static {v0, v4, v5, v6, v7}, Landroid/app/ProgressDialog;->show(Landroid/content/Context;Ljava/lang/CharSequence;Ljava/lang/CharSequence;ZZ)Landroid/app/ProgressDialog;

    move-result-object v1

    .line 48
    .local v1, dlgProgress:Landroid/app/ProgressDialog;
    new-instance v3, Luk/ac/cam/db538/dextertest/MainActivity$1$1;

    invoke-direct {v3, p0, v1, v0}, Luk/ac/cam/db538/dextertest/MainActivity$1$1;-><init>(Luk/ac/cam/db538/dextertest/MainActivity$1;Landroid/app/ProgressDialog;Landroid/content/Context;)V

    .line 77
    .local v3, threadHandler:Landroid/os/Handler;
    new-instance v2, Luk/ac/cam/db538/dextertest/MainActivity$1$2;

    invoke-direct {v2, p0, v0, v3}, Luk/ac/cam/db538/dextertest/MainActivity$1$2;-><init>(Luk/ac/cam/db538/dextertest/MainActivity$1;Landroid/content/Context;Landroid/os/Handler;)V

    .line 139
    .local v2, thread:Ljava/lang/Thread;
    invoke-virtual {v2}, Ljava/lang/Thread;->start()V

    .line 140
    return-void
.end method
