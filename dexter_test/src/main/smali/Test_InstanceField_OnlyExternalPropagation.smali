.class public LTest_InstanceField_OnlyExternalPropagation;
.super Ljava/lang/Object;

# interfaces
.implements LPropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: inherited field, ext. taint only"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "ext. taint should not propagate to int. fields"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 7

    # create Point3 object
    new-instance v2, LMyClass_Point3;
    invoke-direct {v2}, LMyClass_Point3;-><init>()V

    # propagate to external field
    iput p1, v2, LMyClass_Point3;->x:I

    # read internal field
    iget v0, v2, LMyClass_Point3;->z:I

    return v0
    
.end method
