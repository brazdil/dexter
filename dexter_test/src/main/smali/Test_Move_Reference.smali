.class public Luk/ac/cam/db538/dexter/tests/Test_Move_Reference;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Move: reference"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "move-obj rX, new ArrayList([+])"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # size = param % 4
    rem-int/lit8 p1, p1, 0x4

    # create ArrayList
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1, p1}, Ljava/util/ArrayList;-><init>(I)V

    # move it to another register
    move-object v2, v1

    # read the length
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I
    move-result v0
    return v0

.end method
