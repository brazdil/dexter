.class public LTest_BinOp_DivZero;
.super LPropagationTest;



# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LPropagationTest;-><init>()V
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
    .registers 6

    const v1, 32

    # Propagate once

    sub-int p1, p1, p1
    :try_start_1
        div-int v0, v1, p1
        return v0
    :try_end_1
    .catch Ljava/lang/ArithmeticException; {:try_start_1 .. :try_end_1} :handler_1
    :handler_1
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I
    move-result v0

    # ... twice

    sub-int v0, v0, v0
    :try_start_2
        div-int v1, v1, v0
        return v1
    :try_end_2
    .catch Ljava/lang/ArithmeticException; {:try_start_2 .. :try_end_2} :handler_2
    :handler_2
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I
    move-result v0

    # ... three times

    sub-int v0, v0, v0
    :try_start_3
        div-int v0, v1, v0
        return v0
    :try_end_3
    .catch Ljava/lang/ArithmeticException; {:try_start_3 .. :try_end_3} :handler_3
    :handler_3
    move-exception v2
    invoke-virtual {v2}, Ljava/lang/Object;->hashCode()I
    move-result v2

    # ... four times

    sub-int v0, v2, v2
    :try_start_4
        div-int v0, v0, v0
        return v0
    :try_end_4
    .catch Ljava/lang/ArithmeticException; {:try_start_4 .. :try_end_4} :handler_4
    :handler_4
    move-exception v2
    invoke-virtual {v2}, Ljava/lang/Object;->hashCode()I
    move-result v2

    return v2
    
.end method
