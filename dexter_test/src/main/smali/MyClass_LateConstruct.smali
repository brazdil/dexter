.class public abstract LMyClass_LateConstruct;
.super Ljava/lang/Object;

.field private X:I
.field private static Y:I

# direct methods
.method public constructor <init>()V
    .registers 5

    # these instructions can use the THIS argument before it is initialized;
    # careful instrumentation required

    const/4 v0, 4
    iput v0, p0, LMyClass_LateConstruct;->X:I
    iget v1, p0, LMyClass_LateConstruct;->X:I

    move-object v0, p0
    monitor-enter v0
    monitor-exit v0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method
