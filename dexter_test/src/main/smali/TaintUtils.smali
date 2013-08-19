.class public LTaintUtils;
.super Ljava/lang/Object;

# direct methods
.method private constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void

.end method

.method public static taint(I)I
    .registers 1
    
    return p0

.end method

.method public static taint(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1

    return-object p0

.end method

.method public static isTainted(I)Z
    .registers 2

    const/4 v0, 0x0
    return v0

.end method

.method public static isTainted(Ljava/lang/Object;)Z
    .registers 2

    const/4 v0, 0x0
    return v0

.end method
