//
//  MoodTableViewCell.h
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MoodTableViewCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *moodTitle;
@property (strong, nonatomic) IBOutlet UILabel *moodDescription;
@property (strong, nonatomic) IBOutlet UIImageView *moodImageView;

@end
