.class public LTest_InstanceGet_NULL;
.super LNullExceptionTest;

.field private X:I

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNullExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "InstanceGet: tainted NULL object"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "iget rX, NULL[+], ThisClass->X"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    check-cast p1, LTest_InstanceGet_NULL;
    iget v0, p1, LTest_InstanceGet_NULL;->X:I

    return-void
    
.end method
