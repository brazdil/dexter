.class public Luk/ac/cam/db538/dexter/tests/Test_BinOpLit_DivZero;
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
    
    const-string v0, "BinOpLit: div zero"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "div-int/lit rX, [+], #0"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    :try_start
        div-int/lit16 v0, p1, 0x0
        return v0
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :handler

    :handler
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I
    move-result v0
    return v0

.end method
