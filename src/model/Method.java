package model;

public enum Method {
	FullHouse, NakedSingles, HiddenSingles, // fill in digits
	LockedCandidates, NakedPairs, NakedTriples, NakedQuadruples, HiddenPairs, HiddenTriples, HiddenQuadruples, // simple
	WWing, XYWing, XYZWing, // wings
	Skyscarper, TwoStringKite, TurbotFish, EmptyRectangle, // single digit patterns
	XWing, Swordfish, Jellyfish, // fishes
	SashimiXWing, SashimiSwordfish, SashimiJellyfish,
	FinnedXWing, FinnedSwordfish, FinnedJellyfish,
	UniquenessTest1, UniquenessTest2, UniquenessTest3, UniquenessTest4, UniquenessTest5, UniquenessTest6, HiddenRectangle, AvoidableRectangle1, AvoidableRectangle2, // uniqueness
	SueDeCoq,
	SimpleColors, MultiColors; 
}
