.class public LTest_InstanceField_ArrayPrimitive;
.super Ljava/lang/Object;

# interfaces
.implements LPropagationTest;

# instance fields
.field private X:[S

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: primitives array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "this.X = new short[[+]]; return this.X.length;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # size mod 4
    rem-int/lit8 p1, p1, 4

    # create object
    new-array v2, p1, [S

    # propagate
    iput-object v2, p0, LTest_InstanceField_ArrayPrimitive;->X:[S
    iget-object v1, p0, LTest_InstanceField_ArrayPrimitive;->X:[S

    # retrieve some primitive from the object
    array-length v0, v1

    return v0
    
.end method
