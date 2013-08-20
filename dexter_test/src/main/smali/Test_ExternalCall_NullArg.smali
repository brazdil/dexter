.class public LTest_ExternalCall_NullArg;
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
    
    const-string v0, "External call: ext. NULL arg"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "System.getProperty([+], (x = NULL)); return x;"
    return-object v0
    
.end method

.method private static int2string(I)Ljava/lang/String;
    .registers 2

    new-instance v0, Ljava/lang/Integer;
    invoke-direct {v0, p0}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;
    move-result v0

    return-object v0

.end method

.method public propagate(I)I
    .registers 5

    # we use external NULL as an argument of a tainted external call
    # and hope it won't propagate taint

    # create a tainted ArrayList
    rem-int/lit8 p1, p1, 0x4
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1, p1}, Ljava/util/ArrayList;-><init>(I)V

    # create a NULL
    const/4 v0, 0x0

    # pass the NULL to a tainted external call
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    # convert the NULL to an int and return
    invoke-virtual {p0, v0}, LTest_ExternalCall_ExtNullArgRes;->ref2int(Ljava/lang/Object;)I
    move-result v0
    
    return v0
    
.end method

.method public expected()Z
    .registers 1

    # return false
    const/4 v0, 0
    return v0

.end method
