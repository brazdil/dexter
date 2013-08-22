.class public LTest_StaticField_ExternalClass_ReferenceField;
.super LPropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: ext. class, reference field"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "public String field in android.os.Environment"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create String from argument
    invoke-static {p1}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v1

    # propagate
    sput-object v1, Landroid/os/Environment;->DIRECTORY_DCIM:Ljava/lang/String;
    sget-object v0, Landroid/os/Environment;->DIRECTORY_DCIM:Ljava/lang/String;

    # obtain length of the String
    invoke-virtual {v0}, Ljava/lang/String;->length()I
    move-result v2

    return v2
    
.end method
