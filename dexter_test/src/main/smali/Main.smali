.class public LMain;
.super Ljava/lang/Object;

.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

.method public static main([Ljava/lang/String;)V
	.registers 3

    new-instance v0, LTest_FillArrayData;
    invoke-direct {v0}, LTest_FillArrayData;-><init>()V

    new-instance v1, LNoPropagationTestExerciser;
    invoke-direct {v1, v0}, LNoPropagationTestExerciser;-><init>(LPropagationTest;)V

    invoke-virtual {v1}, LTestExerciser;->run()Z

	return-void
.end method
