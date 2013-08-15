.class public Luk/ac/cam/db538/dexter/tests/Test_BinOp_DivZero;
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
    
    const-string v0, "BinOp: div zero"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "div-int rX, [+], rZero"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    :try_start
        const v1, 32
        sub-int p1, p1, p1 # turn the argument into zero
        div-int v0, v1, p1
        return v0
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :handler

    :handler
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I
    move-result v0
    return v0
    
.end method
