//
//  NSAvailableMoods.m
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import "NSAvailableMoods.h"
#import "TBMoods.h"

@implementation NSAvailableMoods
NSAvailableMoods *instance;
NSMutableArray *arrayMoods;

+(NSAvailableMoods *)getInstance{
    if(!instance){
      instance=  [[self alloc]init];
    }
    return  instance;
}
-(id)init{
    instance = [super init];
    
    
    return instance;
}
-(NSMutableArray *)getAvailableMoods{
    if(!arrayMoods){
        [self initMoods];
    }
    return arrayMoods;
}
-(void)initMoods{
    arrayMoods = [[NSMutableArray alloc]init];
    
    NSString *moodName= @"Forest";
    NSString *moodDescription= @"Mix Your Song with Nature Mood";
    NSString *moodImage= @"mood1";
    NSString *BackImage=@"Forest";
    NSString *moodSound= @"mood1";
   // NSString *moodVideo= @"mood1";
    NSString *selectedFlag= @"0";
    
    
    [arrayMoods addObject:[[TBMoods alloc]initwithName:moodName Description:moodDescription Image:moodImage Image1:BackImage andSound :moodSound  selectedFlag:selectedFlag]] ;
    
    moodName= @"Rain & Thunder";
    moodDescription= @"Mix Your Song with Rain Mood";
    moodImage= @"mood3";
    BackImage= @"Rain";
    moodSound= @"mood3";
   // moodVideo= @"mood3";
    selectedFlag= @"1";
    
    
    
    [arrayMoods addObject:[[TBMoods alloc]initwithName:moodName Description:moodDescription Image:moodImage Image1:BackImage andSound :moodSound selectedFlag:selectedFlag]] ;
    moodName= @"Beach";
    moodDescription= @"Mix Your Song with Wave Mood";
    moodImage= @"mood2";
    BackImage= @"Beach";
    moodSound= @"mood2";
  //  moodVideo= @"mood7";
    selectedFlag= @"2";

    
    [arrayMoods addObject:[[TBMoods alloc]initwithName:moodName Description:moodDescription Image:moodImage Image1:BackImage andSound :moodSound selectedFlag:selectedFlag]] ;
    moodName= @"WaterFlow";
    moodDescription= @"Mix your Song with WaterFlow Mood";
    moodImage= @"mood4";
    BackImage= @"WaterFlow";
    moodSound= @"mood1";
  //  moodVideo= @"mood2";
    selectedFlag= @"3";
    
    [arrayMoods addObject:[[TBMoods alloc]initwithName:moodName Description:moodDescription Image:moodImage Image1:BackImage andSound :moodSound  selectedFlag:selectedFlag]] ;


}
@end
