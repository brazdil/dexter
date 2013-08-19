.class public LTest_InstanceField_ExternalClass_ReferenceField;
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
    
    const-string v0, "IField: ext. class, reference field"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "public String field in android.content.pm.FeatureInfo"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, Landroid/content/pm/FeatureInfo;
    invoke-direct {v2}, Landroid/content/pm/FeatureInfo;-><init>()V

    # create String from argument
    invoke-static {p1}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result v1

    # propagate
    iput-object v1, v2, Landroid/content/pm/FeatureInfo;->name:Ljava/lang/String;
    iget-object v0, v2, Landroid/content/pm/FeatureInfo;->name:Ljava/lang/String;

    # once more to force backing up the object register
    iput-object v0, v2, Landroid/content/pm/FeatureInfo;->name:Ljava/lang/String;
    iget-object v2, v2, Landroid/content/pm/FeatureInfo;->name:Ljava/lang/String;

    # obtain length of the String
    invoke-virtual {v2}, Ljava/lang/String;->length()I
    move-result v0

    return v0
    
.end method
