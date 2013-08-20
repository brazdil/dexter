.class public LTest_FillArrayData_NULL;
.super LNullExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNullExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "FillArrayData: NULL array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "fill-array-data NULL[+], :data"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    check-cast p1, [I
    fill-array-data p1, :array_data
    return-void
    
    :array_data
    .array-data 0x4
        0x00t 0x11t 0x22t 0x33t
        0xfft 0xeet 0xddt 0xcct
    .end array-data

.end method
