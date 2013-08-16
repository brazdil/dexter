.class public LMain;
.super Ljava/lang/Object;

.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

.method public static main([Ljava/lang/String;)V
	.registers 3

    new-instance v0, Luk/ac/cam/db538/dexter/tests/Test_FillArrayData;
    invoke-direct {v0}, Luk/ac/cam/db538/dexter/tests/Test_FillArrayData;-><init>()V

    new-instance v1, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;
    invoke-direct {v1, v0}, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V

    invoke-virtual {v1}, Luk/ac/cam/db538/dexter/tests/TestExerciser;->run()Z

	return-void
.end method
