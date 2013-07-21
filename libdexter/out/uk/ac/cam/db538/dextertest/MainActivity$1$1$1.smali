.class Luk/ac/cam/db538/dextertest/MainActivity$1$1$1;
.super Ljava/lang/Object;
.source "MainActivity.java"

# interfaces
.implements Landroid/content/DialogInterface$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Luk/ac/cam/db538/dextertest/MainActivity$1$1;->handleMessage(Landroid/os/Message;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$2:Luk/ac/cam/db538/dextertest/MainActivity$1$1;


# direct methods
.method constructor <init>(Luk/ac/cam/db538/dextertest/MainActivity$1$1;)V
    .registers 2
    .parameter

    .prologue
    .line 1
    iput-object p1, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$1$1;->this$2:Luk/ac/cam/db538/dextertest/MainActivity$1$1;

    .line 54
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/content/DialogInterface;I)V
    .registers 3
    .parameter "dialog"
    .parameter "which"

    .prologue
    .line 57
    invoke-interface {p1}, Landroid/content/DialogInterface;->dismiss()V

    .line 58
    return-void
.end method
