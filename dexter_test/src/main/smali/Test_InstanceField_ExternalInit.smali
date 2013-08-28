.class public LTest_InstanceField_ExternalInit;
.super LNoExceptionTest;

# instance fields
.field private X:Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LNoExceptionTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: external init"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "retrieve and use implicitly initialized field"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    # v0 = new MyClass_ObjectField()
    new-instance v0, LMyClass_ObjectField;
    invoke-direct {v0}, LMyClass_ObjectField;-><init>()V

    # v0 = the contents of the uninitialized field => NULL
    invoke-virtual {v0}, LMyClass_ObjectField;->getX()Ljava/lang/Object;

    # v1 = new ArrayList()
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    # v1.add(v0) => calls t0.get()
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    return-void
    
.end method
