.class public LTestList;
.super Ljava/lang/Object;
.source "TestList.java"


# static fields
.field private static final tests:[LTestExerciser;


# direct methods
.method static constructor <clinit>()V
    .registers 4

    # create array
    const v0, 65
    new-array v0, v0, [LTestExerciser;
    const v1, 0

    new-instance v3, LTest_Move_Primitive;
    invoke-direct {v3}, LTest_Move_Primitive;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_Move_Reference;
    invoke-direct {v3}, LTest_Move_Reference;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_UnaryOp;
    invoke-direct {v3}, LTest_UnaryOp;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_BinOp_Arg1;
    invoke-direct {v3}, LTest_BinOp_Arg1;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_BinOp_Arg2;
    invoke-direct {v3}, LTest_BinOp_Arg2;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_BinOp_DivZero;
    invoke-direct {v3}, LTest_BinOp_DivZero;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_BinOpLit_Standard;
    invoke-direct {v3}, LTest_BinOpLit_Standard;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_Convert_ToShort;
    invoke-direct {v3}, LTest_Convert_ToShort;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_Convert_ToDouble;
    invoke-direct {v3}, LTest_Convert_ToDouble;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_Compare_Arg1;
    invoke-direct {v3}, LTest_Compare_Arg1;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_Compare_Arg2;
    invoke-direct {v3}, LTest_Compare_Arg2;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ExternalCall;
    invoke-direct {v3}, LTest_ExternalCall;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ExternalCall_NullArg;
    invoke-direct {v3}, LTest_ExternalCall_NullArg;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ExternalCall_NullRes;
    invoke-direct {v3}, LTest_ExternalCall_NullRes;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ExternalCall_Exception;
    invoke-direct {v3}, LTest_ExternalCall_Exception;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ExternalCall_ConstructorException;
    invoke-direct {v3}, LTest_ExternalCall_ConstructorException;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_UndecidableCall_Public;
    invoke-direct {v3}, LTest_UndecidableCall_Public;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_UndecidableCall_NonPublic;
    invoke-direct {v3}, LTest_UndecidableCall_NonPublic;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayPrimitive_Length;
    invoke-direct {v3}, LTest_ArrayPrimitive_Length;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayPrimitive_Element;
    invoke-direct {v3}, LTest_ArrayPrimitive_Element;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayPrimitive_Lookup;
    invoke-direct {v3}, LTest_ArrayPrimitive_Lookup;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayPrimitive_FreshLookup;
    invoke-direct {v3}, LTest_ArrayPrimitive_FreshLookup;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_Length;
    invoke-direct {v3}, LTest_ArrayReference_Length;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_ElementExternal;
    invoke-direct {v3}, LTest_ArrayReference_ElementExternal;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_Lookup;
    invoke-direct {v3}, LTest_ArrayReference_Lookup;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_FreshLookup;
    invoke-direct {v3}, LTest_ArrayReference_FreshLookup;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_NestedLookup;
    invoke-direct {v3}, LTest_ArrayReference_NestedLookup;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_ArrayReference_NestedArrays;
    invoke-direct {v3}, LTest_ArrayReference_NestedArrays;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_NameConflict;
    invoke-direct {v3}, LTest_InstanceField_NameConflict;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_NameConflict;
    invoke-direct {v3}, LTest_StaticField_NameConflict;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_Primitive;
    invoke-direct {v3}, LTest_InstanceField_Primitive;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_Primitive;
    invoke-direct {v3}, LTest_StaticField_Primitive;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_External;
    invoke-direct {v3}, LTest_InstanceField_External;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_External;
    invoke-direct {v3}, LTest_StaticField_External;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_Internal;
    invoke-direct {v3}, LTest_InstanceField_Internal;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_Internal;
    invoke-direct {v3}, LTest_StaticField_Internal;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_ArrayPrimitive;
    invoke-direct {v3}, LTest_InstanceField_ArrayPrimitive;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_ArrayPrimitive;
    invoke-direct {v3}, LTest_StaticField_ArrayPrimitive;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_ArrayReference;
    invoke-direct {v3}, LTest_InstanceField_ArrayReference;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_ArrayReference;
    invoke-direct {v3}, LTest_StaticField_ArrayReference;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_Undecidable;
    invoke-direct {v3}, LTest_InstanceField_Undecidable;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_Undecidable;
    invoke-direct {v3}, LTest_StaticField_Undecidable;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_ExternalClass_PrimitiveField;
    invoke-direct {v3}, LTest_InstanceField_ExternalClass_PrimitiveField;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_ExternalClass_PrimitiveField;
    invoke-direct {v3}, LTest_StaticField_ExternalClass_PrimitiveField;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_ExternalClass_ReferenceField;
    invoke-direct {v3}, LTest_InstanceField_ExternalClass_ReferenceField;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_StaticField_ExternalClass_ReferenceField;
    invoke-direct {v3}, LTest_StaticField_ExternalClass_ReferenceField;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_InternalClass_InheritedField;
    invoke-direct {v3}, LTest_InstanceField_InternalClass_InheritedField;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceField_OnlyExternalPropagation;
    invoke-direct {v3}, LTest_InstanceField_OnlyExternalPropagation;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_InstanceOf;
    invoke-direct {v3}, LTest_InstanceOf;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_FillArrayData;
    invoke-direct {v3}, LTest_FillArrayData;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1
    
    new-instance v3, LTest_Immutables;
    invoke-direct {v3}, LTest_Immutables;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_CheckCast_NULL;
    invoke-direct {v3}, LTest_CheckCast_NULL;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_Monitor_NULL;
    invoke-direct {v3}, LTest_Monitor_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ArrayLength_NULL;
    invoke-direct {v3}, LTest_ArrayLength_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ArrayGet_NULL;
    invoke-direct {v3}, LTest_ArrayGet_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ArrayGet_NULL_Index;
    invoke-direct {v3}, LTest_ArrayGet_NULL_Index;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ArrayPut_NULL;
    invoke-direct {v3}, LTest_ArrayPut_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_ArrayPut_NULL_Index;
    invoke-direct {v3}, LTest_ArrayPut_NULL_Index;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_FillArrayData_NULL;
    invoke-direct {v3}, LTest_FillArrayData_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_FillArrayData_Overflow_ExceptionTaint;
    invoke-direct {v3}, LTest_FillArrayData_Overflow_ExceptionTaint;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_FillArrayData_Overflow_DataTaint;
    invoke-direct {v3}, LTest_FillArrayData_Overflow_DataTaint;-><init>()V
    new-instance v2, LPropagationTestExerciser;
    invoke-direct {v2, v3}, LPropagationTestExerciser;-><init>(LPropagationTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_CheckCast_Error;
    invoke-direct {v3}, LTest_CheckCast_Error;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_InstanceGet_NULL;
    invoke-direct {v3}, LTest_InstanceGet_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_InstancePut_NULL;
    invoke-direct {v3}, LTest_InstancePut_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    new-instance v3, LTest_Throw_NULL;
    invoke-direct {v3}, LTest_Throw_NULL;-><init>()V
    new-instance v2, LExceptionTestExerciser;
    invoke-direct {v2, v3}, LExceptionTestExerciser;-><init>(LExceptionTest;)V
    aput-object v2, v0, v1
    add-int/lit8 v1, v1, 1

    sput-object v0, LTestList;->tests:[LTestExerciser;

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
    sget-object v0, LTestList;->tests:[LTestExerciser;

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
    sget-object v0, LTestList;->tests:[LTestExerciser;

    aget-object v0, v0, p0

    invoke-virtual {v0}, LTestExerciser;->getTest()LTest;

    move-result-object v0

    invoke-interface {v0}, LTest;->getDescription()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static getTestList()[LTestExerciser;
    .registers 1

    .prologue
    .line 14
    sget-object v0, LTestList;->tests:[LTestExerciser;

    return-object v0
.end method

.method public static getTestName(I)Ljava/lang/String;
    .registers 2
    .parameter "index"

    .prologue
    .line 26
    sget-object v0, LTestList;->tests:[LTestExerciser;

    aget-object v0, v0, p0

    invoke-virtual {v0}, LTestExerciser;->getTest()LTest;

    move-result-object v0

    invoke-interface {v0}, LTest;->getName()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static runTest(I)Ljava/lang/Boolean;
    .registers 2

    sget-object v0, LTestList;->tests:[LTestExerciser;
    aget-object v0, v0, p0

    invoke-virtual {v0}, LTestExerciser;->run()Z
    move-result v0

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
    move-result-object v0
    return-object v0

.end method

.method public static main([Ljava/lang/String;)V
    .registers 7

    sget-object v6, Ljava/lang/System;->out:Ljava/io/PrintStream;
    sget-object v0, LTestList;->tests:[LTestExerciser;

    # v1 = test count
    # v2 = loop counter
    array-length v1, v0
    const/4 v2, 0x0

    :loop
    if-ge v2, v1, :end

    # v3 = test exerciser object
    aget-object v3, v0, v2

    # v4 = test name
    invoke-virtual {v3}, LTestExerciser;->getTest()LTest;
    move-result-object v4
    invoke-interface {v4}, LTest;->getName()Ljava/lang/String;
    move-result-object v4

    invoke-virtual {v6, v4}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    const-string v4, "... "
    invoke-virtual {v6, v4}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V

    # v5 = test result
    invoke-virtual {v3}, LTestExerciser;->run()Z
    move-result v5

    if-eqz v5, :print_false

    :print_true
    const-string v4, "ok"
    invoke-virtual {v6, v4}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    goto :end_loop

    :print_false
    const-string v4, "ERROR"
    invoke-virtual {v6, v4}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    :end_loop
    add-int/lit8 v2, v2, 1
    goto :loop

    :end
    return-void

.end method