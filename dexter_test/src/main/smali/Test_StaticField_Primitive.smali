.class public Luk/ac/cam/db538/dexter/tests/Test_StaticField_Primitive;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private static X:I

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: primitive"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = [+]; return Test.X;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    sput p1, Luk/ac/cam/db538/dexter/tests/Test_StaticField_Primitive;->X:I
    sget v0, Luk/ac/cam/db538/dexter/tests/Test_StaticField_Primitive;->X:I

    return v0
    
.end method
