.class public Luk/ac/cam/db538/dexter/tests/Test_InstanceField_Internal;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private X:Luk/ac/cam/db538/dexter/tests/MyClass_IntField;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "InstanceField: internal"
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
    new-instance v2, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;
    invoke-direct {v2, p1}, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;-><init>(I)V

    # propagate
    iput v2, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_Internal;->X:Luk/ac/cam/db538/dexter/tests/MyClass_IntField;
    iget v1, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_Internal;->X:Luk/ac/cam/db538/dexter/tests/MyClass_IntField;

    # retrieve some primitive from the object
    invoke-virtual {v1}, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;->getX()I
    move-result v0

    return v0
    
.end method
