.class public LTest_ArrayReference_ElementExternal;
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
    
    const-string v0, "Array: ext. ref, element"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "propagate through Semaphore[0]"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # create object
    new-instance v2, Ljava/util/concurrent/Semaphore;
    invoke-direct {v2, p1}, Ljava/util/concurrent/Semaphore;-><init>(I)V
    move-object p1, v2

    # v0 = new Semaphore[2]
    const/4 v0, 2
    new-array v0, v0, [Ljava/util/concurrent/Semaphore;
    
    # v0[0] = new Semaphore([+])
    const/4 v1, 0
    aput-object p1, v0, v1

    # v0[1] = v0[0] ; the point is to test overwriting index register
    aget-object v1, v0, v1
    const/4 v2, 1
    aput-object v1, v0, v2

    # get v0[0]
    aget-object v0, v0, v2

    # retrieve some primitive from the object
    invoke-virtual {v0}, Ljava/util/concurrent/Semaphore;->getQueueLength()I
    move-result v0

    return v0
    
.end method
