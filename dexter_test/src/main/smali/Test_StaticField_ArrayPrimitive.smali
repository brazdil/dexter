.class public LTest_StaticField_ArrayPrimitive;
.super Ljava/lang/Object;

# interfaces
.implements LPropagationTest;

# instance fields
.field private static X:[S

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: primitives array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = new short[[+]]; return Test.X.length;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # size mod 4
    rem-int/lit8 p1, p1, 4

    # create object
    new-array v2, p1, [S

    # propagate
    sput-object v2, LTest_StaticField_ArrayPrimitive;->X:[S
    sget-object v1, LTest_StaticField_ArrayPrimitive;->X:[S

    # retrieve some primitive from the object
    array-length v0, v1

    return v0
    
.end method
