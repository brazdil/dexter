.class public LMyClass_LateConstruct;
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

.method public constructor <init>(I)V
    .registers 5

    # this constructor moves the THIS reference to a different register
    # and calls the superclass instructor on the copy;
    # need to initialize both afterwards...

    move-object v0, p0
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    const/4 v2, 2
    iput v2, v0, LMyClass_LateConstruct;->X:I

    const/4 v1, 2
    iput v1, p0, LMyClass_LateConstruct;->X:I

    return-void
    
.end method

.method public test()V
    .registers 3

    # this does something similar as above, but with external classes
    # and the NEW_INSTANCE instruction

    # external case

    new-instance v0, Ljava/lang/Object;
    move-object v1, v0
    invoke-direct {v1}, Ljava/lang/Object;-><init>()V
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I

    # internal case
    
    new-instance v0, LMyClass_ObjectField;
    move-object v1, v0
    invoke-direct {v1}, LMyClass_ObjectField;-><init>()V
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I

    return-void

.end method