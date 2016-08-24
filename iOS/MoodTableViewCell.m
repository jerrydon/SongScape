//
//  MoodTableViewCell.m
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import "MoodTableViewCell.h"


@implementation MoodTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
