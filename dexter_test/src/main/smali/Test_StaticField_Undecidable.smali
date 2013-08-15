.class public Luk/ac/cam/db538/dexter/tests/Test_StaticField_Undecidable;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private static X:Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: undecidable"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "field X of type Object"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;
    invoke-direct {v2, p1}, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;-><init>(I)V

    # propagate
    sput-object v2, Luk/ac/cam/db538/dexter/tests/Test_StaticField_Undecidable;->X:Ljava/lang/Object;
    sget-object v1, Luk/ac/cam/db538/dexter/tests/Test_StaticField_Undecidable;->X:Ljava/lang/Object;

    check-cast v1, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;

    # retrieve some primitive from the object
    invoke-virtual {v1}, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;->getX()I
    move-result v0

    return v0
    
.end method
