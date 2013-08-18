.class public LTest_BinOpLit_Standard;
.super Ljava/lang/Object;

# interfaces
.implements LPropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "BinOpLit: standard"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "add-int/lit rX, [+], #1234"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    add-int/lit16 v0, p1, 0x4d2
    return v0
    
.end method
