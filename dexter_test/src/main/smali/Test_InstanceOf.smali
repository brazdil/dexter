.class public LTest_InstanceOf;
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
    
    const-string v0, "InstanceOf"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "return (new ArrayList([+])) instanceof Throwable"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 4

    # create new ArrayList with the argument
    rem-int/lit8 p1, p1, 4
    new-instance v0, Ljava/util/ArrayList;
    invoke-direct {v0, p1}, Ljava/util/ArrayList;-><init>(I)V

    # is it an instance of Throwable?
    instance-of v0, v0, Ljava/lang/Throwable;

    return v0

.end method
