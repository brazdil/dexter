.class public Luk/ac/cam/db538/dexter/tests/Test_Const;
.super Ljava/lang/Object;
.source "Test_Const.java"

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/SourceTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public generate()I
    .registers 2

    const v0, 0xdec0ded
    return v0
    
.end method

.method public getName()Ljava/lang/String;
    .registers 2

    const-string v0, "0xDEC0DED source"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Only for unit testing. The constant should get tainted"
    return-object v0
    
.end method
