.class public LTest_InstanceField_Internal;
.super LPropagationTest;


# instance fields
.field private X:LMyClass_IntField;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: internal"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "this.X = new MyClass([+]); return this.X.get();"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, LMyClass_IntField;
    invoke-direct {v2, p1}, LMyClass_IntField;-><init>(I)V

    # propagate
    iput-object v2, p0, LTest_InstanceField_Internal;->X:LMyClass_IntField;
    iget-object v1, p0, LTest_InstanceField_Internal;->X:LMyClass_IntField;

    # retrieve some primitive from the object
    invoke-virtual {v1}, LMyClass_IntField;->getX()I
    move-result v0

    return v0
    
.end method
