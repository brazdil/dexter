package uk.ac.cam.db538.dexter.transform;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.InstructionList;
import uk.ac.cam.db538.dexter.dex.code.elem.DexCodeElement;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryEnd;
import uk.ac.cam.db538.dexter.dex.code.elem.DexTryStart;
import uk.ac.cam.db538.dexter.utils.Pair;
import uk.ac.cam.db538.dexter.utils.Triple;

public final class TryBlockSplitter {

	private TryBlockSplitter() { }
	
	public static DexCode checkAndFixTryBlocks(DexCode code) {
		InstructionList insns = code.getInstructionList();
		
		while(true) {
			TryBlockList tryBlocks = getAllTryBlocks(insns);
			checkBlocksValid(tryBlocks);
			
			Pair<TryBlockInfo, TryBlockInfo> nestedBlocks = getNestedBlocks(tryBlocks);
			if (nestedBlocks == null)
				break;
			else if (!isAllowed(nestedBlocks.getValB()))
				throw new AssertionError("Only CatchAll blocks are allowed to be nested");
			
			insns = fixNestedBlocks(insns, nestedBlocks.getValA(), nestedBlocks.getValB());
		}

		return new DexCode(code, insns);
	}

	static class TryBlockInfo extends Triple<DexTryStart, Integer, Integer> {
		public TryBlockInfo(DexTryStart valA, Integer valB, Integer valC) {
			super(valA, valB, valC);
		} 
	}
	
	static class TryBlockList extends ArrayList<TryBlockInfo> {
		private static final long serialVersionUID = 1L; 
	}
	
	/*
	 * Returns all blocks together with the corresponding indices of the Start and End markers
	 */
	static TryBlockList getAllTryBlocks(InstructionList insns) {
		TryBlockList blocks = new TryBlockList();
		for (DexCodeElement insn : insns) {
			if (insn instanceof DexTryStart) {
				DexTryStart block = (DexTryStart) insn;
				blocks.add(new TryBlockInfo(block, insns.getIndex(block), insns.getIndex(block.getEndMarker())));
			}
		}
					
		return blocks;
	}
	
	static void checkBlocksValid(TryBlockList blocks) {
		for (TryBlockInfo block1 : blocks) {

			int iStart1 = block1.getValB(); 
			int iEnd1 = block1.getValC();

			// check that end comes after start 
			if (iStart1 >= iEnd1)
				throw new AssertionError("Start of a TRY block comes after its end");
			
			for (TryBlockInfo block2 : blocks) {
				if (block1 == block2)
					continue;
				
				int iStart2 = block2.getValB(); 
				int iEnd2 = block2.getValC();
				
				// check that they do not overlap
				// i.e. (S1 < S2 < E1 => S1 < E2 < E1)
				if (iStart1 < iStart2 && iStart2 < iEnd1 && iEnd2 >= iEnd1)
					throw new AssertionError("Two TRY blocks must not overlap");
			}
		}
	}
	
	private static boolean areNested(TryBlockInfo outer, TryBlockInfo inner) {
		int iStart1 = outer.getValB(); 
		int iEnd1 = outer.getValC();
		int iStart2 = inner.getValB(); 
		int iEnd2 = inner.getValC();
		
		return iStart1 <= iStart2 && iEnd2 <= iEnd1;
	}
	
	/*
	 * Picks two innermost nest blocks, or returns null if no such exist
	 */
	static Pair<TryBlockInfo, TryBlockInfo> getNestedBlocks(TryBlockList blocks) {
		boolean foundAny = false;
		TryBlockInfo outer = null, inner = null;
		
		// try each pair
		for (TryBlockInfo block1 : blocks) {
			for (TryBlockInfo block2 : blocks) {
				if (block1 == block2)
					continue;
				
				if (areNested(block1, block2)) {
					if (!foundAny) {
						outer = block1;
						inner = block2;
						foundAny = true;
					} else if (areNested(inner, block1)) {
						// block1 is contained inside inner
						// and block2 is contained inside block1
						// => we found a deeper pair
						outer = block1;
						inner = block2;
					}
				}
			}
		}
		
		if (foundAny)
			return Pair.create(outer, inner);
		else
			return null;
	}
	
	static boolean isAllowed(TryBlockInfo inner) {
		DexTryStart start = inner.getValA();
		return start.getCatchHandlers().isEmpty() && start.getCatchAllHandler() != null; 
	}
	
	static InstructionList fixNestedBlocks(InstructionList insns, TryBlockInfo outer, TryBlockInfo inner) {
		assert(areNested(outer, inner));
		
		// split the outer block
		
		DexTryStart startOuter = outer.getValA();
		DexTryEnd endOuter = startOuter.getEndMarker();
		int idOuter = endOuter.getId();
		
		DexTryEnd endOuter_1 = new DexTryEnd(idOuter);
		DexTryEnd endOuter_2 = new DexTryEnd(idOuter);
		
		DexTryStart startOuter_1 = new DexTryStart(startOuter, endOuter_1);
		DexTryStart startOuter_2 = new DexTryStart(startOuter, endOuter_2);
		
		// replace the markers in the instruction list,
		// start from the end to preserve indices
		
		int iStartOuter = outer.getValB();
		int iEndOuter = outer.getValC();
		int iStartInner = inner.getValB();
		int iEndInner = inner.getValC();
		
		LinkedList<DexCodeElement> newInsns = new LinkedList<DexCodeElement>(insns);
		
		newInsns.remove(iEndOuter);
		newInsns.add(iEndOuter, endOuter_2);
		newInsns.add(iEndInner + 1, startOuter_2);
		newInsns.add(iStartInner, endOuter_1);
		newInsns.remove(iStartOuter);
		newInsns.add(iStartOuter, startOuter_1);
		
		return new InstructionList(newInsns);
	}
}

