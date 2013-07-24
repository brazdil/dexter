.class public Luk/ac/cam/db538/dexter/tests/Test_BinOp_Arg2;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "BinOp: arg2"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "add-int rX, rConst, [+]"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    const v1, 0xDEADBEEF
    add-int v0, v1, p1
    return v0
    
.end method
