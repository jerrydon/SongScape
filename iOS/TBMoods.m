//
//  TBMoods.m
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import "TBMoods.h"

@implementation TBMoods
-(id)initwithName:(NSString *)name Image1 :(NSString * ) BgImage andSound:(NSString *)sound  selectedFlag: (NSString *)sFlag;{
    id instance =[self init];
    
    _moodName           =name;
//    _moodDescription    =description;
//    _moodImage          =image;
    _BackImage          =BgImage;
    //_moodVideo          =video;
    _moodSound          =sound;
    _selectedFlag       =sFlag;
    return instance;
}
@end
