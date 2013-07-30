.class public Luk/ac/cam/db538/dexter/tests/TestList;
.super Ljava/lang/Object;
.source "TestList.java"


# static fields
.field private static final tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;


# direct methods
.method static constructor <clinit>()V
    .registers 4

    # create array
    const v0, 13
    new-array v0, v0, [Luk/ac/cam/db538/dexter/tests/TestExerciser;

    const v1, 0x0
    new-instance v2, Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Const;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Const;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/SourceTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/SourceTest;)V
    aput-object v2, v0, v1

    const v1, 0x1
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Move_Primitive;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Move_Primitive;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x2
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_UnaryOp;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_UnaryOp;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x3
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_BinOp_Arg1;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_BinOp_Arg1;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x4
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_BinOp_Arg2;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_BinOp_Arg2;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x5
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_BinOp_DivZero;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_BinOp_DivZero;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x6
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_BinOpLit_Standard;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_BinOpLit_Standard;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x7
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_BinOpLit_DivZero;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_BinOpLit_DivZero;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x8
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Convert_ToShort;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Convert_ToShort;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0x9
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Convert_ToDouble;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Convert_ToDouble;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0xA
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Compare_Arg1;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Compare_Arg1;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0xB
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_Compare_Arg2;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_Compare_Arg2;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    const v1, 0xC
    new-instance v2, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
    new-instance v3, Luk/ac/cam/db538/dexter/tests/Test_ArrayPrimitive_Length;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/Test_ArrayPrimitive_Length;-><init>()V
    invoke-direct {v2, v3}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    aput-object v2, v0, v1

    sput-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static getTestCount()Ljava/lang/Integer;
    .registers 1

    .prologue
    .line 18
    sget-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    array-length v0, v0

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    return-object v0
.end method

.method public static getTestDescription(I)Ljava/lang/String;
    .registers 2
    .parameter "index"

    .prologue
    .line 30
    sget-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    aget-object v0, v0, p0

    invoke-virtual {v0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;->getTest()Luk/ac/cam/db538/dexter/tests/Test;

    move-result-object v0

    invoke-interface {v0}, Luk/ac/cam/db538/dexter/tests/Test;->getDescription()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static getTestList()[Luk/ac/cam/db538/dexter/tests/TestExerciser;
    .registers 1

    .prologue
    .line 14
    sget-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    return-object v0
.end method

.method public static getTestName(I)Ljava/lang/String;
    .registers 2
    .parameter "index"

    .prologue
    .line 26
    sget-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    aget-object v0, v0, p0

    invoke-virtual {v0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;->getTest()Luk/ac/cam/db538/dexter/tests/Test;

    move-result-object v0

    invoke-interface {v0}, Luk/ac/cam/db538/dexter/tests/Test;->getName()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static runTest(I)Ljava/lang/Boolean;
    .registers 2
    .parameter "index"

    .prologue
    .line 22
    sget-object v0, Luk/ac/cam/db538/dexter/tests/TestList;->tests:[Luk/ac/cam/db538/dexter/tests/TestExerciser;

    aget-object v0, v0, p0

    invoke-virtual {v0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;->run()Z

    move-result v0

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v0

    return-object v0
.end method
