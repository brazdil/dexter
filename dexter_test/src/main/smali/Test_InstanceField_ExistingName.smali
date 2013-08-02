.class public Luk/ac/cam/db538/dexter/tests/Test_InstanceField_NameConflict;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private X:I
.field private t_X:Ljava/lang/Object;
.field private t_X$0:Ljava/lang/Object;
.field private t_X$1:Ljava/lang/Object;
.field private t_X$2:Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "InstanceField: name conflict"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "fields t_X, t_X${0,1,2} exist already"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    iput p1, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_NameConflict;->X:I
    iget v0, p0, Luk/ac/cam/db538/dexter/tests/Test_InstanceField_NameConflict;->X:I

    return v0
    
.end method
