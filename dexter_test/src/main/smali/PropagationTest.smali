.class public abstract LPropagationTest;
.super Ljava/lang/Object;

# interfaces
.implements LTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public abstract propagate(I)I
.end method

.method public expected()Z
	.registers 1

	# return true
	const/4 v0, 1
	return v0

.end method

.method protected int2null(I)Ljava/lang/Object;
	.registers 4

    # create ArrayList
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    # add NULL
    const/4 v0, 0x0
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    # now retrieve it again with tainted index
    sub-int p1, p1, p1
    invoke-virtual {v1, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;
    move-result-object v0

    return-object v0

.end method

.method public static ref2int(Ljava/lang/Object;)I
	.registers 3

	new-instance v0, Ljava/lang/Object;
	invoke-direct {v0}, Ljava/lang/Object;-><init>()V

	invoke-virtual {v0, p0}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
	move-result v1

	return v1

.end method

.method protected int2string(I)Ljava/lang/String;
    .registers 3

    new-instance v0, Ljava/lang/Integer;
    invoke-direct {v0, p1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;
    move-result-object v0

    return-object v0

.end method

