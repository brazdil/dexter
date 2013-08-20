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

.method protected ref2int(Ljava/lang/Object;)I
	.registers 3

	new-instance v0, Ljava/lang/Object;
	invoke-direct {v0}, Ljava/lang/Object;-><init>()V

	invoke-virtual {v0, p1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
	move-result v1

	return v1

.end method