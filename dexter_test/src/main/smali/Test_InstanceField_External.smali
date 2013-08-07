.class public Luk/ac/cam/db538/dexter/tests/Test_InstanceField_External;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private X:Ljava/util/concurrent/Semaphore;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: external"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "this.X = new Semaphore([+]); return this.X.getQueueLength();"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, Ljava/util/concurrent/Semaphore;
    invoke-direct {v2, p1}, Ljava/util/concurrent/Semaphore;-><init>(I)V

    # propagate
    iput-object v2, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_External;->X:Ljava/util/concurrent/Semaphore;
    iget-object v1, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_External;->X:Ljava/util/concurrent/Semaphore;

    # retrieve some primitive from the object
    invoke-virtual {v1}, Ljava/util/concurrent/Semaphore;->getQueueLength()I
    move-result v0

    return v0
    
.end method
