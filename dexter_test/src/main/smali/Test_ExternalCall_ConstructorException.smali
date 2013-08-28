.class public LTest_ExternalCall_ConstructorException;
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
    
    const-string v0, "External call: superclass constructor exception"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "new FutureTask(NULL)"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 4

    check-cast p1, Ljava/util/concurrent/Callable;

    # v1 = new MyClass_FutureTask([+]) // should throw
    new-instance v1, LMyClass_FutureTask;
    invoke-direct {v1, p1}, LMyClass_FutureTask;-><init>(Ljava/util/concurrent/Callable;)V

    return-void

.end method
