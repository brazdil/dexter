.class public LTest_ArrayLength_NULL;
.super LNullExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNullExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "ArrayLength: NULL array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "array-length [+]"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    check-cast p1, [Ljava/lang/Object;
    array-length v0, p1
    return-void
    
.end method
