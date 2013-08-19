.class public LTest_StaticField_Internal;
.super LPropagationTest;


# instance fields
.field private static X:LMyClass_IntField;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: internal"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = new MyClass([+]); return Test.X.get();"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, LMyClass_IntField;
    invoke-direct {v2, p1}, LMyClass_IntField;-><init>(I)V

    # propagate
    sput-object v2, LTest_StaticField_Internal;->X:LMyClass_IntField;
    sget-object v1, LTest_StaticField_Internal;->X:LMyClass_IntField;

    # retrieve some primitive from the object
    invoke-virtual {v1}, LMyClass_IntField;->getX()I
    move-result v0

    return v0
    
.end method
