.class public LTest_ArrayReference_Length;
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
    
    const-string v0, "Array: reference, length"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "new-array rY, [+] % 4, [Ljava/lang/Object;\narray-length rX, rY"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    rem-int/lit8 p1, p1, 0x4                # param as length (mod 4)
    new-array p1, p1, [Ljava/lang/Object;   # array Object[]
    array-length v0, p1
    return v0
    
.end method
