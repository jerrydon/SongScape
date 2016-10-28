//
//  TBMoods.h
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TBMoods : NSObject
-(id)initwithName:(NSString *)name Image1 :(NSString * ) BgImage andSound:(NSString *)sound  selectedFlag: (NSString *)sFlag;
@property NSString *moodName;
//@property NSString *moodDescription;
//@property NSString *moodImage;
@property NSString *BackImage;
//@property NSString *moodVideo;
@property NSString *moodSound;
@property NSString *selectedFlag;
@end
